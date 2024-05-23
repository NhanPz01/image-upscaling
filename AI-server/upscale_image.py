from flask import Flask, request, jsonify
import cv2
from flask_cors import CORS
import numpy as np
from realesrgan import RealESRGANer
from basicsr.archs.rrdbnet_arch import RRDBNet
import torch
import time
import io
import base64
import mysql.connector

app = Flask(__name__)
CORS(app)

db_config = {
    "host": "localhost",
    "user": "root",  
    "password": "",  
    "database": "image_scaling" 
}

model_path = "RealESRGAN_x2plus.pth"

upscaler = RealESRGANer(
    scale=2,
    model_path=model_path,
    model=RRDBNet(
        num_in_ch=3, num_out_ch=3, num_feat=64, num_block=23, num_grow_ch=32, scale=2
    ),
    tile=0,
    tile_pad=10,
    pre_pad=10,
    half=False,
    device="cuda" if torch.cuda.is_available() else "cpu",
)

@app.route('/upscale', methods=['POST'])
def upscale_image():
    try:
        user_id = request.form.get('user_id')
        if not user_id:
            return jsonify({'error': 'User ID is required'}), 400

        if 'image' not in request.files:
            return jsonify({'error': 'No image file provided'}), 400

        image_file = request.files['image']

        image_id = save_image_info_to_db(user_id)
        if not image_id:
            return jsonify({'error': 'Failed to save image info to database'}), 500

        image_data = image_file.read()
        image_np = cv2.imdecode(np.frombuffer(image_data, np.uint8), cv2.IMREAD_COLOR)

        start_time = time.time()
        output_image, _ = upscaler.enhance(image_np)
        end_time = time.time()
        print(f"Upscaling took {end_time - start_time} seconds")

        _, buffer = cv2.imencode('.jpg', output_image)
        image_base64 = base64.b64encode(buffer).decode('utf-8')

        update_image_in_db(image_id, image_base64)

        return jsonify({'image_base64': image_base64, 'image_id': image_id})

    except Exception as e:
        return jsonify({'error': str(e)}), 500


def save_image_info_to_db(user_id):
    try:
        connection = mysql.connector.connect(**db_config)
        cursor = connection.cursor()

        insert_query = """
            INSERT INTO image (user_id, status) 
            VALUES (%s, 'WAITING')
        """
        cursor.execute(insert_query, (user_id,))
        connection.commit()
        image_id = cursor.lastrowid
        return image_id

    except Exception as e:
        print(f"Error saving image info to database: {e}")
        return None
    finally:
        if connection and connection.is_connected():
            cursor.close()
            connection.close()

def update_image_in_db(image_id, image_base64):
    try:
        connection = mysql.connector.connect(**db_config)
        cursor = connection.cursor()

        update_query = """
            UPDATE image 
            SET status = 'DONE', url = %s
            WHERE id = %s
        """
        image_data = base64.b64decode(image_base64)
        cursor.execute(update_query, (image_data, image_id))
        connection.commit()

    except Exception as e:
        print(f"Error updating image in database: {e}")
    finally:
        if connection and connection.is_connected():
            cursor.close()
            connection.close()


if __name__ == '__main__':
    app.run(debug=True, threaded=True)
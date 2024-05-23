from flask import Flask, request, jsonify, send_file
import cv2
from flask_cors import CORS
import numpy as np
from realesrgan import RealESRGANer
from basicsr.archs.rrdbnet_arch import RRDBNet
import requests
import torch
import time
import io
import base64

app = Flask(__name__)
CORS(app)

# Path to the Real-ESRGAN model
model_path = "RealESRGAN_x2plus.pth"

# Create the Real-ESRGAN upscaler
upscaler = RealESRGANer(
    scale=2,  # Upscaling factor (e.g., 2 for 2x upscaling)
    model_path=model_path,
    model=RRDBNet(
        num_in_ch=3, num_out_ch=3, num_feat=64, num_block=23, num_grow_ch=32, scale=2
    ),  # Define model architecture
    tile=0,  # Tile size for processing large images (0 for no tiling)
    tile_pad=10,  # Padding for tiling
    pre_pad=10,  # Padding before upscaling
    half=False,  # Whether to use half precision
    device="cuda" if torch.cuda.is_available() else "cpu",
)

@app.route('/upscale', methods=['POST'])
def upscale_image():
    """
    Endpoint for upscaling an image.

    Returns:
        A JSON response containing the upscaled image as a base64 encoded string.
    """
    try:
        data = request.get_json()
        image_url = data.get('image_url')
        if not image_url:
            return jsonify({'error': 'Image URL is missing'}), 400

        # Download the image from the URL
        response = requests.get(image_url)
        response.raise_for_status()  # Raise an exception for bad status codes
        image_data = response.content

        # Convert the image data to a NumPy array
        image = cv2.imdecode(np.frombuffer(image_data, np.uint8), cv2.IMREAD_COLOR)

        # Upscale the image
        start_time = time.time()
        output_image, _ = upscaler.enhance(image)
        end_time = time.time()
        elapsed_time = end_time - start_time
        print(f"Upscaling took {elapsed_time} seconds")

        # Encode the upscaled image to base64
        _, buffer = cv2.imencode('.jpg', output_image)
        image_base64 = base64.b64encode(buffer).decode('utf-8')

        return jsonify({'image_base64': image_base64})

    except Exception as e:
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    app.run(debug=True)
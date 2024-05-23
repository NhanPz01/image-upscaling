package model;

public class Image {
    public int id;
    public String base64Image;

    public Image(int id, String base64Image) {
        this.id = id;
        this.base64Image = base64Image;
    }

    public int getId() {
        return id;
    }

    public String getBase64Image() {
        return base64Image;
    }
}


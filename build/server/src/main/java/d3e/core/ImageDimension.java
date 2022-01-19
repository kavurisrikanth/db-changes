package d3e.core;

public class ImageDimension {
    private int width;
    private int height;

    public ImageDimension() {
    }

    public ImageDimension(int width, int height) {
        this.setWidth(width);
        this.setHeight(height);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}

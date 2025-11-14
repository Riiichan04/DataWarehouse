package dto;

public class Source {
    int sourceId;
    String name;
    String url;

    public Source(int sourceId, String name, String url) {
        this.sourceId = sourceId;
        this.name = name;
        this.url = url;
    }

    public Source() {
    }

    @Override
    public String toString() {
        return "Source{" +
                "sourceId=" + sourceId +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

package pl.piotrgorczyca.myrunnerapp.adapters;

/**
 * Created by Piotr on 2015-12-29. Enjoy!
 */
public class CommentsListItem {
    private String name;
    private String content;
    private String created_at;

    public CommentsListItem(String name, String content, String created_at) {
        this.setContent(content);
        this.setName(name);
        this.setCreated_at(created_at);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}

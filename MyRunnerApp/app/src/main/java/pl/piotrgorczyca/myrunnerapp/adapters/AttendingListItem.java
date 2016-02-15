package pl.piotrgorczyca.myrunnerapp.adapters;

/**
 * Created by Piotr on 2015-12-30. Enjoy!
 */
public class AttendingListItem {
    private String name;
    private int user_id;

    public AttendingListItem(String name, int user_id) {
        this.setName(name);
        this.setUserId(user_id);

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }
}

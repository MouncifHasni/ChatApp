package in.tvac.akshaye.lapitchat;

public class Groupes {

    public String name;
    public String admin;
    public String image;

    public Groupes(){

    }

    public Groupes(String name, String admin, String image) {
        this.name = name;
        this.admin = admin;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

package billeterie.model;

public class UserProfileData {
    private final String username;
    private final String fullname;
    private final String email;
    private final String phone;
    private final String birthdate;
    private final String address;
    private final String profileImagePath;

    public UserProfileData(String username, String fullname, String email, String phone,
            String birthdate, String address, String profileImagePath) {
        this.username = username;
        this.fullname = fullname;
        this.email = email;
        this.phone = phone;
        this.birthdate = birthdate;
        this.address = address;
        this.profileImagePath = profileImagePath;
    }

    public String getUsername() {
        return username;
    }

    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getAddress() {
        return address;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }
}

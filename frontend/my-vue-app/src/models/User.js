export default class User{
    constructor(data) {
        this.id = data?.id;
        this.login = data?.login;
        this.password = data?.password;
        this.role = data?.role;
        this.mail = data?.mail;
        this.phoneNumber = data?.phoneNumber;
    }
}
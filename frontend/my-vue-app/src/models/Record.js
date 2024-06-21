export default class Record{
    constructor(data) {
        this.id = data?.id;
        this.price = data?.price;
        this.place = data?.place;
        this.status = data?.status;
        this.doctorId = data?.doctorId;
        this.userId = data?.userId;
    }
}
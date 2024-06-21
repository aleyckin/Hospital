export default class Notification{
    constructor(data) {
        this.id = data?.id;
        this.status = data?.status;
        this.textInfo = data?.textInfo;
        this.recordId = data?.recordId;
    }
}
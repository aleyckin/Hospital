<template>
    <div>
        <div class="container mt-4">
            <h1 class="text-center mb-4">Таблица всех записей</h1>
            <table class="table table-striped" v-if="records.length > 0">
                <thead>
                    <tr>
                        <th>Номер записи</th>
                        <th>Цена</th>
                        <th>Расположение</th>
                        <th>Статус</th>
                        <th>Доктор</th>
                        <th>User ID</th>
                        <th>Начало</th>
                        <th>Конец</th>
                        <th>Действия</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="record in records" :key="record.id">
                        <td>{{ record.id }}</td>
                        <td>{{ record.price }}</td>
                        <td>{{ record.place }}</td>
                        <td>{{ record.status }}</td>
                        <td>{{ getDoctorName(record.doctorId) }}</td>
                        <td>{{ record.userId }}</td>
                        <td>{{ formatDateTime(record.startTime) }}</td>
                        <td>{{ formatDateTime(record.endTime) }}</td>
                        <td>
                            <button class="btn btn-primary mr-2" @click="openModal('edit', record)">Изменить</button>
                            <button class="btn btn-danger" @click="deleteRecord(record.id)">Удалить</button>
                        </td>
                    </tr>
                </tbody>
            </table>

            <!-- Модальное окно для редактирования -->
            <div class="modal" :class="{ 'is-active': isModalActive, 'modal-center': isModalActive }">
                <div class="modal-background" @click="closeModal"></div>
                <div class="modal-card">
                    <header class="modal-card-head">
                        <p class="modal-card-title">{{ modalTitle }}</p>
                        <button class="delete" aria-label="close" @click="closeModal"></button>
                    </header>
                    <section class="modal-card-body">
                        <form @submit.prevent="handleSubmit">
                            <div class="field">
                                <label class="label">Цена:</label>
                                <input class="input" type="number" v-model="editedRecord.price" readonly>
                            </div>
                            <div class="field">
                                <label class="label">Расположение:</label>
                                <div class="control">
                                    <input class="input" type="text" v-model="editedRecord.place" readonly>
                                </div>
                            </div>
                            <div class="field">
                                <label class="label">Доктор:</label>
                                <div class="control">
                                    <input class="input" type="text" :value="getDoctorName(editedRecord.doctorId)" readonly>
                                </div>
                            </div>
                            <div class="field">
                                <label class="label">Статус:</label>
                                <div class="control">
                                    <div class="select">
                                        <select v-model="editedRecord.status" required>
                                            <option value="Active">Active</option>
                                            <option value="Inactive">Inactive</option>
                                            <!-- Добавьте другие статусы, если необходимо -->
                                        </select>
                                    </div>
                                </div>
                            </div>
                            <button class="button is-primary" type="submit">{{ modalAction }}</button>
                        </form>
                    </section>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import axios from "axios";
import Header from "../components/Header.vue";

export default {
    components: {
        Header
    },
    data() {
        return {
            token: localStorage.getItem("token"),
            placeOptions: ["Zasviyazhye", "Kindyakovka", "Center", "Sever"],
            records: [],
            doctors: [],
            editedRecord: {
                id: null,
                price: null,
                place: "Zasviyazhye", // Default value
                status: "Active", // Default value
                doctorId: null,
                userId: null,
                startTime: null,
                endTime: null
            },
            isModalActive: false,
            modalTitle: "",
            modalAction: "",
            URL: "http://localhost:8080/api/",
            putParams: {
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + localStorage.getItem("token"),
                },
            },
            delParams: {
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + localStorage.getItem("token"),
                },
            },
            getParams: {
                headers: {
                    "Authorization": "Bearer " + localStorage.getItem("token"),
                },
            },
        };
    },
    created() {
        this.getRecords();
        this.getDoctors();
    },
    methods: {
        getAuthHeader() {
            return { Authorization: `Bearer ${this.token}` };
        },
        getRecords() {
            axios.get(this.URL + "record", { headers: this.getAuthHeader() })
            .then(response => {
                this.records = response.data;
            })
            .catch(error => {
                console.error(error);
            });
        },
        getDoctors() {
            axios.get(this.URL + "doctor", this.getParams)
                .then(response => {
                    this.doctors = response.data;
                })
                .catch(error => {
                    console.error(error);
                });
        },
        deleteRecord(id) {
            axios.delete(this.URL + `record/${id}`, this.delParams)
                .then(() => {
                    this.getRecords();
                })
                .catch(error => {
                    console.error(error);
                });
        },
        editRecord() {
            axios.put(this.URL + `record/updateStatus/${this.editedRecord.id}`, this.editedRecord, this.putParams)
                .then(() => {
                    this.getRecords();
                    this.closeModal();
                })
                .catch(error => {
                    console.error(error);
                });
        },
        openModal(action, record = null) {
            if (action === "edit" && record) {
                this.modalTitle = "Изменить запись";
                this.modalAction = "Сохранить";
                this.editedRecord = { ...record };
            }
            this.isModalActive = true;
        },
        closeModal() {
            this.isModalActive = false;
        },
        handleSubmit() {
            if (this.modalAction === "Сохранить") {
                this.editRecord();
            }
        },
        getDoctorName(doctorId) {
            const doctor = this.doctors.find(doc => doc.id === doctorId);
            return doctor ? doctor.name : 'Неизвестный доктор';
        },
        formatDateTime(dateTimeStr) {
            const dateTime = new Date(dateTimeStr);
            return `${dateTime.toLocaleDateString()} ${dateTime.toLocaleTimeString()}`;
        }
    }
};
</script>

<style scoped>
.modal {
    display: none;
    background-color: rgba(0, 0, 0, 0.5);
    position: fixed;
    top: 0;
    right: 0;
    bottom: 0;
    left: 0;
    z-index: 999;
    align-items: center;
    justify-content: center;
}

.modal.is-active {
    display: flex;
}

.modal-card {
    max-width: 90%;
    width: 50%;
    background-color: white;
    border-radius: 5px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
    margin: auto;
    position: relative;
}

.modal-card-head {
    background-color: #f0f0f0;
    padding: 15px;
    border-bottom: 1px solid #ccc;
    display: flex;
    justify-content: space-between;
}

.modal-card-body {
    padding: 15px;
}

.modal-card-body .field {
    margin-bottom: 10px;
}

.modal-center {
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    z-index: 999;
}
</style>

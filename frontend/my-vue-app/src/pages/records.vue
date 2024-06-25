<template>
    <div>
        <div class="container mt-4">
            <h1 class="text-center mb-4">Таблица записей</h1>
            <button class="btn btn-primary mr-2" @click="openModal('create')">Добавить</button>
            <table class="table table-striped" v-if="records.length > 0">
                <thead>
                    <tr>
                        <th>Номер записи</th>
                        <th>Цена</th>
                        <th>Расположение</th>
                        <th>Статус</th>
                        <th>Доктор</th>
                        <th>Начало</th>
                        <th>Конец</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="record in records" :key="record.id">
                        <td>{{ record.id }}</td>
                        <td>{{ record.price }}</td>
                        <td>{{ record.place }}</td>
                        <td>{{ record.status }}</td>
                        <td>{{ formatDoctorInfo(record.doctorId) }}</td>
                        <td>{{ formatDateTime(record.startTime) }}</td>
                        <td>{{ formatDateTime(record.endTime) }}</td>
                        <td>
                            <button class="btn btn-primary mr-2" @click="openModal('edit', record)">Изменить</button>
                            <button class="btn btn-danger" @click="deleteRecord(record.id)">Удалить</button>
                        </td>
                    </tr>
                    
                </tbody>
            </table>

            <!-- Модальное окно для создания и редактирования -->
            <div class="modal" :class="{ 'is-active': isModalActive, 'modal-center': isModalActive }">
                <div class="modal-background" @click="closeModal"></div>
                <div class="modal-card">
                    <header class="modal-card-head">
                        <p class="modal-card-title">{{ modalTitle }}</p>
                        <button class="delete" aria-label="close" @click="closeModal"></button>
                    </header>
                    <section class="modal-card-body">    
                        <!-- Форма редактирования записи -->
                        <form @submit.prevent="handleSubmit">
                            <div class="field">
                                <label class="label">Цена:</label>
                                <input class="input" type="number" v-model="editedRecord.price" required readonly>
                            </div>
                            <div class="field">
                                <label class="label">Расположение:</label>
                                <div class="control">
                                    <div class="select">
                                        <select v-model="editedRecord.place" @change="filterDoctorsByPlace" required>
                                            <option v-for="place in placeOptions" :key="place" :value="place">
                                                {{ place }}
                                            </option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                            <div class="field">
                                <label class="label">Доктор:</label>
                                <div class="control">
                                    <div class="select">
                                        <select v-model="editedRecord.doctorId" @change="updatePrice" required>
                                            <option v-for="doctor in filteredDoctors" :key="doctor.id" :value="doctor.id">
                                                {{ doctor.specialization }} {{ doctor.name }}
                                            </option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                            <div class="field">
                                <label class="label">Начало:</label>
                                <input class="input" type="datetime-local" v-model="editedRecord.startTime" required>
                            </div>
                            <div class="field">
                                <label class="label">Конец:</label>
                                <input class="input" type="datetime-local" v-model="editedRecord.endTime" required>
                            </div>
                            <hr>
                            <button class="button is-primary" type="submit">{{ modalAction }}</button>
                        </form>
                    </section>
                </div>
            </div>
        </div>
        <div v-if="errorMessage" class="alert alert-danger alert-dismissible fade show" role="alert">
            {{ errorMessage }}
            <button type="button" class="close" @click="errorMessage = ''">
                <span aria-hidden="true">&times;</span>
            </button>
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
            user: localStorage.getItem("user"),
            userId: null,
            placeOptions: ["Zasviyazhye", "Kindyakovka", "Center", "Sever"],
            records: [],
            doctors: [],
            filteredDoctors: [],
            currentUser: null,
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
            errorMessage: "",
            maxDuration: 2 * 60 * 60 * 1000, // Максимальная длительность 2 часа в миллисекундах
            URL: "http://localhost:8080/api/",
            postParams: {
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + localStorage.getItem("token"),
                },
            },
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
        this.getUserIdByLogin(this.user)
            .then( (userId) => {
                this.userId = userId;
                this.editedRecord.userId = userId;
                this.getRecords(this.userId);
                this.getDoctors();
            });    
    },
    methods: {
        getUserIdByLogin(login) {
            return axios.get(`http://localhost:8080/api/user/id`, { 
                headers: this.getAuthHeader(), 
                params: { login: login } 
            })
            .then(response => {
                return response.data; // Возвращаем ID пользователя
            })
            .catch(error => {
                console.error(error);
                return null;
            });
        },
        getAuthHeader() {
            return { Authorization: `Bearer ${this.token}` };
        },
        getRecords(userId) {
            axios.get(this.URL + "record/user", { headers: this.getAuthHeader(), params: { userId: Number(userId) } })
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
                    this.filterDoctorsByPlace();
                })
                .catch(error => {
                    console.error(error);
                });
        },
        addRecord() {
            // Проверка startTime и endTime
            if (!this.isValidTimeRange()) {
                this.errorMessage = "Пожалуйста, убедитесь, что время начала меньше времени окончания и длительность не превышает 2 часов.";
                return;
            }

            axios.post(this.URL + "record", this.editedRecord, this.postParams)
                .then(() => {
                    this.getRecords(this.userId);
                    this.closeModal();
                })
                .catch(error => {
                    if (error.response && error.response.status === 400 && error.response.data.message) {
                        this.errorMessage = error.response.data.message;
                        this.closeModal();
                    } else {
                        this.errorMessage = "Произошла ошибка при добавлении записи.";
                        this.closeModal();
                    }
                    console.error(error);
                });
        },
        deleteRecord(id) {
            axios.delete(this.URL + `record/${id}`, this.delParams)
                .then(() => {
                    this.getRecords(this.userId);
                })
                .catch(error => {
                    console.error(error);
                });
        },
        editRecord() {
            if (!this.isValidTimeRange()) {
                this.errorMessage = "Пожалуйста, убедитесь, что время начала меньше времени окончания и длительность не превышает 2 часов.";
                this.closeModal();
                return;
            }

            axios.put(this.URL + `record/${this.editedRecord.id}`, this.editedRecord, this.putParams)
                .then(() => {
                    this.getRecords(this.userId);
                    this.closeModal();
                })
                .catch(error => {
                    console.error(error);
                });
        },
        openModal(action, record = null) {
            if (action === "create") {
                this.modalTitle = "Добавить запись";
                this.modalAction = "Создать";
                this.editedRecord = {
                    id: null,
                    price: null,
                    place: "Zasviyazhye", // Default value
                    status: "Active", // Default value
                    doctorId: null,
                    userId: this.userId,
                    startTime: null,
                    endTime: null
                };
            } else if (action === "edit") {
                this.modalTitle = "Редактировать запись";
                this.modalAction = "Сохранить";
                this.editedRecord = { ...record };
            }
            this.isModalActive = true;
        },
        closeModal() {
            this.isModalActive = false;
        },
        handleSubmit() {
            if (this.modalAction === "Создать") {
                this.addRecord();
            } else if (this.modalAction === "Сохранить") {
                this.editRecord();
            }
        },
        filterDoctorsByPlace() {
            this.filteredDoctors = this.doctors.filter(doctor => doctor.place === this.editedRecord.place);
        },
        updatePrice() {
            if (this.editedRecord.doctorId) {
                axios.get(this.URL + "record/price", { params: { doctorId: this.editedRecord.doctorId } })
                    .then(response => {
                        this.editedRecord.price = response.data;
                    })
                    .catch(error => {
                        console.error(error);
                    });
            }
        },
        formatDateTime(dateTimeStr) {
            const dateTime = new Date(dateTimeStr);
            return `${dateTime.toLocaleDateString()} ${dateTime.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}`;
        },
        isValidTimeRange() {
            const startTime = new Date(this.editedRecord.startTime).getTime();
            const endTime = new Date(this.editedRecord.endTime).getTime();

            if (startTime >= endTime) {
                return false; // startTime должно быть раньше endTime
            }

            if (endTime - startTime !== this.maxDuration) {
                return false; // Длительность не должна отличаться от 2-ух часов
            }

            return true;
        },
        formatTimeRange(startTime, endTime) {
            const formattedStartTime = this.formatDateTime(startTime);
            const formattedEndTime = this.formatDateTime(endTime);

            return `${formattedStartTime} - ${formattedEndTime}`;
        },
        formatDoctorInfo(doctorId) {
            const doctor = this.doctors.find(doc => doc.id === doctorId);
            return doctor ? `${doctor.specialization} ${doctor.name}` : 'Неизвестно';
        },
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
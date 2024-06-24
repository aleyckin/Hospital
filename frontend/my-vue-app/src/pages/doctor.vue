<template>
    <div class="container mt-4">
        <h1 class="text-center mb-4">Doctor Table</h1>
        <button class="btn btn-primary mr-2" @click="openModal('create')">Добавить</button>
        <table class="table table-striped">
            <thead>
                <tr>
                    <th>Имя</th>
                    <th>Расположение</th>
                    <th>Действия</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="doctor in doctors" :key="doctor.id">
                    <td>{{ doctor.name }}</td>
                    <td>{{ doctor.place }}</td>
                    <td>
                        <button class="btn btn-primary mr-2" @click="openModal('edit', doctor)">Изменить</button>
                        <button class="btn btn-danger" @click="deleteDoctor(doctor.id)">Удалить</button>
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
                    <form @submit.prevent="handleSubmit">
                        <div class="field">
                            <label class="label">Имя:</label>
                            <input class="input" type="text" v-model="editedDoctor.name" required>
                        </div>
                        <div class="field">
                            <label class="label">Расположение:</label>
                            <div class="control">
                                <div class="select">
                                    <select v-model="editedDoctor.place" required>
                                        <option v-for="option in placeOptions" :key="option" :value="option">
                                            {{ option }}
                                        </option>
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
</template>

<script>
import axios from "axios";
import Doctor from "../models/Doctor";

export default {
    data() {
        return {
            doctors: [],
            editedDoctor: {
                id: null,
                name: "",
                place: "Zasviyazhye", // Default value
            },
            isModalActive: false,
            modalTitle: "",
            modalAction: "",
            placeOptions: ["Zasviyazhye", "Kindyakovka", "Center", "Sever"], // Enum options
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
        this.getDoctors();
    },
    methods: {
        getDoctors() {
            axios.get(this.URL + "doctor", this.getParams)
                .then(response => {
                    this.doctors = response.data;
                })
                .catch(error => {
                    console.error(error);
                });
        },
        addDoctor(doctor) {
            axios.post(this.URL + "doctor", doctor, this.postParams)
                .then(() => {
                    this.getDoctors();
                    this.closeModal();
                })
                .catch(error => {
                    console.error(error);
                });
        },
        editDoctor(doctor) {
            axios.put(this.URL + `doctor/${doctor.id}`, doctor, this.putParams)
                .then(() => {
                    const index = this.doctors.findIndex((s) => s.id === doctor.id);
                    if (index !== -1) {
                        // Обновляем элемент в массиве напрямую
                        this.doctors[index] = doctor;
                    }
                    this.closeModal();
                })
                .catch(error => {
                    console.error(error);
                });
        },
        deleteDoctor(id) {
            axios.delete(this.URL + `doctor/${id}`, this.delParams)
                .then(() => {
                    this.getDoctors();
                })
                .catch(error => {
                    console.error(error);
                });
        },
        openModal(action, doctor = null) {
            if (action === "create") {
                this.modalTitle = "Добавить доктора";
                this.modalAction = "Создать";
                this.editedDoctor = { name: "", place: "Zasviyazhye" }; // Default value
            } else if (action === "edit" && doctor) {
                this.modalTitle = "Изменить доктора";
                this.modalAction = "Сохранить";
                this.editedDoctor = { ...doctor };
            }
            this.isModalActive = true;
        },
        closeModal() {
            this.isModalActive = false;
        },
        handleSubmit() {
            if (this.modalAction === "Создать") {
                this.addDoctor(this.editedDoctor);
            } else if (this.modalAction === "Сохранить") {
                this.editDoctor(this.editedDoctor);
            }
        },
    },
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

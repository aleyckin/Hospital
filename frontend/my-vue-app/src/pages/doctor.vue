<template>
    <div class="container mt-4">
        <h1 class="text-center mb-4">Doctor Table</h1>
        <button class="btn btn-primary mr-2" @click="openModal('create')">Добавить</button>
        <table class="table table-striped">
            <thead>
            <tr>
                <th>Имя</th>
                <th>Расположение</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="doctor in doctors" :key="doctor.id">
                <td>{{ doctor.name }}</td>
                <td>{{ doctor.place }}</td>
                <td>
                    <td>
                        <button class="btn btn-primary mr-2" @click="openModal('edit', doctor)">Изменить</button>
                    </td>
                    <td>
                        <button class="btn btn-danger" @click="deleteDoctor(doctor.id)">Удалить</button>
                    </td>
                </td>
            </tr>
            </tbody>
        </table>
    </div>
    <div class="modal" tabindex="-1" id="editModal">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Доктор</h5>
                </div>
                <div class="modal-body">
                    <form>
                        <div class="form-group">
                            <label for="name">Имя:</label>
                            <input type="text" class="form-control" id="name" name="name" v-model="editedDoctor.name">
                            <label for="place">Расположение:</label>
                            <select class="form-control" id="place" name="place" v-model="editedDoctor.place">
                                <option v-for="option in locationOptions" :key="option" :value="option">{{ option }}</option>
                            </select>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="editModal" @click="closeModal()">Закрыть</button>
                    <button type="button" class="btn btn-primary" v-if="editedDoctor.status === 'create'" @click="addDoctor(editedDoctor)">Создать</button>
                    <button type="button" class="btn btn-primary" v-else @click="editDoctor(editedDoctor)">Сохранить</button>
                </div>
            </div>
        </div>
    </div>
    
    <div class="modal" tabindex="-1" id="ModelForRecords">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Записи</h5>
                </div>
                <div class="modal-body">
                    <form>
                        <div class="form-group">
                            <table class="table table-striped">
                                <thead>
                                <tr>
                                    <th>Номер записи</th>
                                    <th>Цена</th>
                                    <th>Расположение</th>
                                    <th>Статус</th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr v-for="record in records" :key="record.id">
                                    <td>{{ record.id }}</td>
                                    <td>{{ record.price }}</td>
                                    <td>{{ record.place }}</td>
                                    <td>{{ record.status }}</td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="ModelForRecords" @click="closeModelForRecords()">Закрыть</button>
                </div>
            </div>
        </div>
    </div>
</template>
<script>
import 'axios';
import axios from "axios";
import Doctor from "../models/Doctor"
import Record from '../models/Record';
export default {
    created() {
        this.getDoctors();
    },
    mounted() {
        const addModal = document.getElementById('editModal');
        addModal.addEventListener('shown.bs.modal', function () {
        })
    },

    data() {
        return{
            locationOptions: ["HOSPITAL", "CLINIC", "HOME"],
            doctors: [],
            URL: "http://localhost:8080/api/",
            doctor: new Doctor(),
            editedDoctor: new Doctor(),
            records: [],
            postParams: {
                            method:"POST",
                            headers:{
                                "Content-Type":"application/json",
                                "Authorization": "Bearer " + localStorage.getItem("token"),
                            }
                        },
                        putParams: {
                            method:"PUT",
                            headers:{
                                "Content-Type":"application/json",
                                "Authorization": "Bearer " + localStorage.getItem("token"),
                            },
                        },
                        delParams: {
                            method:"DELETE",
                            headers:{
                                "Content-Type":"application/json",
                                "Authorization": "Bearer " + localStorage.getItem("token"),
                            }
                        },
                        getParams: {
                            method:"GET",
                            headers:{
                                "Authorization": "Bearer " + localStorage.getItem("token"),
                            }
                        },
        }
    },
    methods: {
        getDoctors(){
            axios.get(this.URL + "doctor", this.getParams)
                .then(response => {
                    this.doctors = response.data;
                    console.log(response.data);
                })
                .catch(error => {
                    console.log(error);
                });
        },
        addDoctor(doctor) {
            console.log(doctor);
            axios
                .post(this.URL + "doctor", doctor, this.postParams)
                .then(() => {
                    this.getDoctors();
                    this.closeModal();
                })
                .catch((error) => {
                    console.log(error);
                });
        },
        deleteDoctor(id){
            axios.delete(this.URL + `doctor/${id}`, this.delParams)
                .then(() =>{
                    this.getDoctors();
                })
        },
        openModal(status, doctor = null) {
            if (status === "create") {
                this.editedDoctor = new Doctor();
                this.editedDoctor.status = "create";
            } else if (status === "edit" && doctor) {
                this.editedDoctor = { ...doctor };
                this.editedDoctor.status = "edit";
            }

            document.getElementById("editModal").style.display = "block";
        },
        closeModal() {
            document.getElementById("editModal").style.display = "none";
        },
        editDoctor(doctor) {
            axios.put(this.URL + `doctor/${doctor.id}`, doctor, this.putParams)
                .then(() => {
                    const index = this.doctors.findIndex((s) => s.id === doctor.id);
                    if (index !== -1) {
                        this.doctors[index] = { ...doctor };
                    }
                    this.closeModal();
                    this.getDoctors();
                })
                .catch((error) => {
                    console.log(error);
                });
        },
    }
}
</script>
<style>
    
</style>
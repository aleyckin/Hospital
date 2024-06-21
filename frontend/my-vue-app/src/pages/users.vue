<template >
    <table class="table table-striped table-bordered table-hover">
        <thead>
        <tr>
            <th scope="col">ID</th>
            <th scope="col">Логин</th>
            <th scope="col">Роль</th>
            <th scope="col">Почта</th>
            <th scope="col">Мобильный телефон</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="(user, index) in users" :key="index">
            <td>{{ user.id }}</td>
            <td>{{ user.login }}</td>
            <td>{{ user.role }}</td>
            <td>{{ user.mail }}</td>
            <td>{{ user.phoneNumber }}</td>
        </tr>
        </tbody>
    </table>
</template>
<script>
import axios from 'axios'
export default {
    data(){
        return{
            users: []
        }
    },
    created(){
        const getTokenForHeader = function () {
            return "Bearer " + localStorage.getItem("token");
        }
        const requestParams = {
            method: "GET",
            headers: {
                "Authorization": getTokenForHeader(),
            }
        };
        axios.get(`http://localhost:8080`, requestParams)
            .then(response => {
                this.users = response.data;
            })
            .catch(error => {
                console.log(error);
            });
    },
    methods:{

    }
}
</script>
<style >

</style>
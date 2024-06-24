<template>
    <div>
      <div v-if="loading" class="text-center mt-3">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Загрузка...</span>
        </div>
      </div>
      <div v-else>
        <table class="table table-striped table-bordered table-hover">
          <thead class="thead-dark">
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
      </div>
    </div>
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
<style scoped>
/* Дополнительные стили для улучшения внешнего вида таблицы */
.table {
  margin-top: 20px;
  font-size: 0.875rem;
}

.table th,
.table td {
  vertical-align: middle;
}

.table th {
  font-weight: bold;
  text-align: center;
}

.table td {
  text-align: center;
}

.spinner-border {
  margin-top: 20px;
}
</style>
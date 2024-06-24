<template>
    <div class="container mt-4">
      <h1 class="text-center mb-4">Смена пароля</h1>
      <form @submit.prevent="resetPassword">
        <div class="mb-3">
          <label for="newPassword" class="form-label">Новый пароль</label>
          <input 
            type="password" 
            class="form-control" 
            id="newPassword" 
            v-model="newPassword" 
            required 
          />
        </div>
        <button type="submit" class="btn btn-primary">Сменить пароль</button>
      </form>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  
  export default {
    data() {
        return {
            newPassword: '',
            token: this.$route.query.token // предполагается, что токен будет передан в URL
        };
    },
    methods: {
        async resetPassword() {
        console.log('Отправляемые данные:', {
            token: this.token,
            newPassword: this.newPassword
        });

        try {
            const response = await axios.post('http://localhost:8080/reset-password', {
                token: this.token,
                newPassword: this.newPassword
            }, {
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            console.log('Ответ от сервера:', response.data);
            alert('Пароль успешно изменен');
            this.logout(); // перенаправление на страницу входа
        } catch (error) {
            if (error.response) {}
        }
        },
        logout() {
            localStorage.removeItem("token");
            localStorage.removeItem("user");
            localStorage.removeItem("role");
            this.token = null;
            this.user = '';
            this.role = '';
            this.$router.push('/login');
        }
    }
  }
  </script>
  
  <style>
  .container {
    max-width: 600px;
    margin-top: 50px;
  }
  </style>
  
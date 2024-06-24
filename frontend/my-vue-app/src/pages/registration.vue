<template>
    <div class="registration-container">
      <div class="registration">
        <form @submit.prevent="registration" class="registration__form" action="#">
          <div class="form__login">
            <input class="login-input form-control" v-model="login" id="login" required validate="false" placeholder="Логин" type="text" name="Логин">
          </div>
          <div class="form__password">
            <input class="password-input form-control" v-model="password" id="password" required validate="false" placeholder="Пароль" type="password" name="Пароль">
          </div>
          <div class="form__mail">
            <input class="mail-input form-control" v-model="mail" id="mail" required validate="false" placeholder="Почта" type="email" name="Почта">
          </div>
          <div class="form__phoneNumber">
            <input class="phoneNumber-input form-control" v-model="phoneNumber" id="phoneNumber" required validate="false" placeholder="Мобильный телефон" type="text" name="Номер телефона">
          </div>
          <div class="registration__buttons">
            <button type="submit" class="registration__confirm" :disabled="loading">Зарегистрироваться</button>
            <a class="registration__login" href="/login">Уже есть аккаунт</a>
          </div>
          <div v-if="loading" class="text-center mt-3">
            <div class="spinner-border text-primary" role="status">
              <span class="visually-hidden">Loading...</span>
            </div>
          </div>
        </form>
      </div>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  
  export default {
    data() {
      return {
        login: "",
        password: "",
        mail: "",
        phoneNumber: "",
        loading: false
      };
    },
    methods: {
      registration() {
        if (!this.validatePhoneNumber(this.phoneNumber)) {
          alert('Пожалуйста, введите корректный номер телефона.');
          return;
        }
  
        this.loading = true; // Устанавливаем состояние загрузки
  
        let user = {
          login: this.login,
          password: this.password,
          passwordConfirm: this.password,
          mail: this.mail,
          phoneNumber: this.phoneNumber
        };
  
        axios.post(`http://localhost:8080/sign_up`, user)
          .then(response => {
            if (response.status === 200) {
              alert(response.data);
              this.$router.push('/login');
            }
          })
          .catch(error => {
            console.log(error);
          })
          .finally(() => {
            this.loading = false; // Сбрасываем состояние загрузки после получения ответа
          });
      },
      validatePhoneNumber(phoneNumber) {
        const phoneRegex = /^((8|\+7)[\- ]?)?(\(?\d{3}\)?[\- ]?)?[\d\- ]{7,10}$$/im;
        return phoneRegex.test(phoneNumber);
      }
    }
  };
  </script>
  
  <style scoped>
  .registration-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
  }
  
  .registration {
    width: 300px;
    padding: 20px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    background-color: #fff;
    border-radius: 8px;
  }
  
  .registration__form {
    display: flex;
    flex-direction: column;
  }
  
  .form__login,
  .form__password,
  .form__mail,
  .form__phoneNumber {
    margin-bottom: 15px;
  }
  
  .registration__buttons {
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  
  .registration__confirm {
    background-color: #007bff;
    color: white;
    border: none;
    padding: 10px 20px;
    border-radius: 5px;
    cursor: pointer;
    text-align: center;
    margin-bottom: 15px; /* Добавлен отступ снизу */
  }
  
  .registration__login {
    color: #007bff;
    text-decoration: none;
  }
  
  .registration__login:hover {
    text-decoration: underline;
  }
  </style>
  
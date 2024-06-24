<template>
  <div class="container mt-4">
    <h1 class="text-center mb-4">Профиль пользователя</h1>
    <form @submit.prevent="updateProfile">
      <div class="mb-3">
        <label for="login" class="form-label">Логин</label>
        <input 
          type="text" 
          class="form-control" 
          id="login" 
          v-model="user.login" 
          required 
          readonly 
        />
      </div>
      <div class="mb-3">
        <label for="mail" class="form-label">Электронная почта</label>
        <input 
          type="email" 
          class="form-control" 
          id="mail" 
          v-model="user.mail" 
          required 
        />
      </div>
      <div class="mb-3">
        <label for="phoneNumber" class="form-label">Номер телефона</label>
        <input 
          type="tel" 
          class="form-control" 
          id="phoneNumber" 
          v-model="user.phoneNumber" 
          required 
        />
      </div>
      <button type="submit" class="btn btn-primary">Обновить</button>
      <button type="button" class="btn btn-secondary" @click="sendResetPasswordEmail">
        <span v-if="loadingResetPasswordEmail" class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
        Изменить пароль
      </button>
    </form>

    <div v-if="showPasswordModal" class="modal" tabindex="-1" role="dialog" style="display: block;">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Изменить пароль</h5>
            <button type="button" class="close" @click="showPasswordModal = false">
              <span>&times;</span>
            </button>
          </div>
          <div class="modal-body">
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
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showPasswordModal = false">Отмена</button>
            <button type="button" class="btn btn-primary" @click="updatePassword">Сохранить</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  created() {
      this.loadUserProfile();
  },
  data() {
      return {
          user: {
              login: localStorage.getItem("user"),
              password: '',
              role: localStorage.getItem("role"),
              mail: '',
              phoneNumber: ''
          },
          token: localStorage.getItem('token'),
          showPasswordModal: false,
          newPassword: '',
          loadingResetPasswordEmail: false // Добавляем состояние загрузки
      };
  },
  methods: {
      getAuthHeader() {
          return { Authorization: `Bearer ${this.token}` };
      },
      loadUserProfile() {
          axios.get(`http://localhost:8080/profile/${this.user.login}`, { headers: this.getAuthHeader() })
              .then(response => {
                  this.user.login = response.data.login;
                  this.user.mail = response.data.mail;
                  this.user.phoneNumber = response.data.phoneNumber;
              })
              .catch(error => {
                  console.log(error);
              });
      },
      updateProfile() {
          if (this.validatePhoneNumber(this.user.phoneNumber)) {
              axios.put(`http://localhost:8080/profile/${this.user.login}`, this.user, { headers: this.getAuthHeader() })
                  .then(response => {
                      alert('Профиль обновлен успешно');
                  })
                  .catch(error => {
                      console.log(error);
                  });
          } else {
              alert('Номер телефона введен неверно');
          }
      },
      sendResetPasswordEmail() {
          this.loadingResetPasswordEmail = true; // Устанавливаем состояние загрузки

          axios.post(`http://localhost:8080/email/reset-password/${this.user.mail}`, {}, { headers: this.getAuthHeader() })
              .then(response => {
                  alert('Ссылка для смены пароля отправлена на вашу электронную почту');
              })
              .catch(error => {
                  console.log(error);
              })
              .finally(() => {
                  this.loadingResetPasswordEmail = false; // Сбрасываем состояние загрузки после получения ответа
              });
      },
       validatePhoneNumber(phoneNumber) {
            const phoneRegex = /^((8|\+7)[\- ]?)?(\(?\d{3}\)?[\- ]?)?[\d\- ]{7,10}$$/im;
            return phoneRegex.test(phoneNumber);
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

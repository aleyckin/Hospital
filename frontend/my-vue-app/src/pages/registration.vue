<template>
    <div class="registration">
        <form @submit.prevent="registration" class="registration__form" action="#">
            <div class="form__login">
                <input class="login-input form-control" v-model="login" id="login" required="" validate="false" placeholder="Логин" type="text" name="Логин">
            </div>
            <div class="form__password">
                <input class="password-input form-control" v-model="password" id="password" required="" validate="false" placeholder="Пароль" type="password" name="Пароль">
            </div>
            <div class="form__mail">
                <input class="mail-input form-control" v-model="mail" id="mail" required="" validate="false" placeholder="Почта" type="email" name="Почта">
            </div>
            <div class="form__phoneNumber">
                <input class="phoneNumber-input form-control" v-model="phoneNumber" id="phoneNumber" required="" validate="false" placeholder="Мобильный телефон" type="text" name="Номер телефона">
            </div>
            <div class="registration__buttons">
                <button class="registration__confirm" id="reg_btn" type="submit">Зарегистрироваться</button><a class="registration__login" href="/login">Уже есть аккаунт</a>
            </div>
        </form>
    </div>
</template>
<script>
import axios from 'axios';
export default {
    data(){
        return{
            login: "",
            password: "",
            mail: "",
            phoneNumber: ""
        }
    },
    mounted() {
        const queryParams = new URLSearchParams(window.location.search);
        const token = queryParams.get('token');
        if (token) {
            axios.get(`http://localhost:8080/registrationConfirm?token=${token}`)
                .then(response => {
                    alert('Регистрация подтверждена. Теперь вы можете войти в систему.');
                    this.$router.push('/login');
                })
                .catch(error => {
                    console.error(error);
                    alert('Ошибка подтверждения регистрации.');
                });
        }
    },
    methods:{
        registration(){
            if (!this.validatePhoneNumber(this.phoneNumber)) {
                alert('Пожалуйста, введите корректный номер телефона.');
                return;
            }
            let user = {
                login: this.login,
                password: this.password,
                passwordConfirm: this.password,
                mail: this.mail,
                phoneNumber: this.phoneNumber
            }
            axios.post(`http://localhost:8080/sign_up`, user)
                .then((response) => {
                    if (response.status === 200) {
                        alert(response.data);
                    }
                })
                .catch(error => {
                    console.log(error);
                });
            this.$router.push('/login');
        },
        validatePhoneNumber(phoneNumber) {
            const phoneRegex = /^((8|\+7)[\- ]?)?(\(?\d{3}\)?[\- ]?)?[\d\- ]{7,10}$$/im;
            return phoneRegex.test(phoneNumber);
        }
    }
}
</script>
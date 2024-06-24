<template>
    <div class="login-container">
        <div class="login">
            <form @submit.prevent class="login__form" action="/">
                <div class="form__login">
                    <input  class="login-input form-control"
                            id="login" v-model="newUser.login"
                            validate="false"
                            placeholder="Логин"
                            type="text"
                            name="Логин">
                </div>
                <div class="form__password">
                    <input  class="password-input form-control"
                            id="password" v-model="newUser.password"
                            validate="false"
                            placeholder="Пароль"
                            type="password"
                            name="Пароль">
                </div>
                <div class="login__buttons mt-5">
                    <button type="submit" class="login__confirm" id="login__confirm" @click="login">Войти</button>
                    <a class="login__registration" href="registration">Регистрация</a>
                </div>
            </form>
        </div>
    </div>
</template>
<script>
import axios from 'axios';
export default {
    mounted(){

    },
    data(){
        return{
            newUser: new Object,
            token: '',
            user: '',
            role: '',
        }
    },
    methods: {
        getUserData() {
            this.user = localStorage.getItem("user") || '';
            this.role = localStorage.getItem("role") || '';
        },
        async login() {
            await this.getRole()
            try {
                const response = await axios.post(`http://localhost:8080/jwt/login`, this.newUser);
                if (response.status === 200) {
                    localStorage.setItem("token", response.data);
                    localStorage.setItem("user", this.newUser.login);
                    this.token = response.data;
                    this.user = this.newUser.login;
                    this.$emit('login', {
                        token: this.token,
                        user: this.newUser.login,
                        role: this.role,
                    });
                    this.getUserData();
                    this.$router.push("/profile")
                } else {
                    localStorage.removeItem("token");
                    localStorage.removeItem("user");
                    localStorage.removeItem("role");
                }
            } catch (error) {
                console.log(error);
            }
        },

        async getRole(){
            await axios.get(`http://localhost:8080/who_am_i?userLogin=${this.newUser.login}`)
                .then((response) => {
                    localStorage.setItem("role", response.data)
                    this.role = response.data
                })
                .catch(error => {
                    console.log(error);
                })
        }
    }
}
</script>
<style scoped>
.login-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh; /* 100% высоты viewport */
}

.login {
    width: 300px; /* Задайте желаемую ширину формы */
    padding: 20px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    background-color: #fff;
    border-radius: 8px;
}

.login__form {
    display: flex;
    flex-direction: column;
}

.form__login,
.form__password {
    margin-bottom: 15px;
}

.login__buttons {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.login__confirm {
    background-color: #007bff;
    color: white;
    border: none;
    padding: 10px 20px;
    border-radius: 5px;
    cursor: pointer;
    text-align: center;
}

.login__registration {
    color: #007bff;
    text-decoration: none;
}

.login__registration:hover {
    text-decoration: underline;
}
</style>
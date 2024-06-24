<template>
    <div class="login-container">
        <div class="login">
            <form @submit.prevent="login" class="login__form">
                <div class="form__login">
                    <input  class="login-input form-control"
                            id="login" v-model="newUser.login"
                            placeholder="Логин"
                            type="text"
                            name="Логин"
                            required>
                </div>
                <div class="form__password">
                    <input  class="password-input form-control"
                            id="password" v-model="newUser.password"
                            placeholder="Пароль"
                            type="password"
                            name="Пароль"
                            required>
                </div>
                <div class="login__buttons mt-5">
                    <button type="submit" class="login__confirm" id="login__confirm">Войти</button>
                    <a class="login__registration" href="registration">Регистрация</a>
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
            newUser: {
                login: '',
                password: ''
            },
            token: '',
            user: '',
            role: ''
        };
    },
    methods: {
        async login() {
            try {
                const response = await axios.post(`http://localhost:8080/jwt/login`, this.newUser);
                if (response.status === 200) {
                    localStorage.setItem("token", response.data);
                    localStorage.setItem("user", this.newUser.login);
                    this.token = response.data;
                    this.user = this.newUser.login;
                    
                    await this.getRole(); // Ожидание получения роли пользователя
                    
                    this.$emit('login', {
                        token: this.token,
                        user: this.newUser.login,
                        role: this.role,
                    });
                    this.$router.push("/profile");
                }
            } catch (error) {
                if (error.response) {
                    // Сервер вернул ошибку с кодом состояния
                    if (error.response.status === 401) {
                        if (error.response.data === 'Incorrect password.') {
                            alert('Неправильный пароль.');
                        } else {
                            alert('Пользователь не найден.');
                        }
                    } else if (error.response.status === 403) {
                        alert('Аккаунт не активирован. Пожалуйста, проверьте вашу электронную почту для активации аккаунта.');
                    } else {
                        alert('Произошла ошибка при входе. Пожалуйста, попробуйте снова позже.');
                    }
                } else if (error.request) {
                    // Ошибка запроса без ответа от сервера
                    alert('Не удалось получить ответ от сервера. Пожалуйста, попробуйте снова позже.');
                } else {
                    // Общие ошибки при обработке запроса
                    alert('Произошла ошибка при входе. Пожалуйста, попробуйте снова позже.');
                }
                // Очистка локального хранилища в случае ошибки
                localStorage.removeItem("token");
                localStorage.removeItem("user");
                localStorage.removeItem("role");
            }
        },
        async getRole() {
            try {
                const response = await axios.get(`http://localhost:8080/who_am_i?userLogin=${this.newUser.login}`);
                localStorage.setItem("role", response.data);
                this.role = response.data;
            } catch (error) {
                console.error(error);
            }
        }
    }
}
</script>

<style scoped>
.login-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
}

.login {
    width: 300px;
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

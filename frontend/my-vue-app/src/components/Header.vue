<template>
    <nav v-if="token" class="navbar navbar-expand-lg navbar-light bg-light">
      <div class="container-fluid">
        <div class="collapse navbar-collapse" id="navbarNav">
          <ul class="navbar-nav justify-content-center">
            <li class="nav-item">
              <router-link to="/profile" class="nav-link">Профиль</router-link>
              <router-link v-if="role === 'ADMIN'" to="/doctor" class="nav-link">Врачи</router-link>
              <router-link v-if="role === 'ADMIN'" to="/users" class="nav-link">Пользователи</router-link>
              <button type="button" class="btn btn-secondary" @click="logout()">Разлогиниться</button>
            </li>
          </ul>
          <div class="navbar-text ms-auto">
            <span v-if="user">Пользователь: {{ user }}</span>
            <span v-if="role"> | Роль: {{ role }}</span>
          </div>
        </div>
      </div>
    </nav>
  </template>


<style>
.navbar-brand {
    font-size: 2rem;
    font-weight: bold;
}

.nav-link {
    color: black;
    font-weight: bold;
    text-transform: uppercase;
    transition: color 0.3s ease-in-out;
}

.nav-link:hover {
    color: #dc3545;
}
</style>


<script>
export default {
    data() {
        return {
            token: localStorage.getItem("token"),
            user: '',
            role: ''
        };
    },
    mounted() {
        if (this.token) {
            this.getUserData();
        }
    },
    methods: {
        getUserData() {
            this.user = localStorage.getItem("user") || '';
            this.role = localStorage.getItem("role") || '';
        },
        logout() {
            localStorage.removeItem("token");
            localStorage.removeItem("user");
            localStorage.removeItem("role");
            this.$router.push('/login');
            this.getUserData();
        }
    }
}
</script>
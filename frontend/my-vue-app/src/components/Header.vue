<template>
  <nav v-if="token" class="navbar navbar-expand-lg navbar-light bg-light">
    <div class="container-fluid">
      <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav justify-content-center">
          <li class="nav-item">
            <router-link to="/profile" class="nav-link">Профиль</router-link>
            <router-link to="/records" class="nav-link">Записи</router-link>
            <router-link v-if="role === 'ADMIN'" to="/allrecords" class="nav-link">Все записи</router-link>
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
      user: localStorage.getItem("user") || '',
      role: localStorage.getItem("role") || ''
    };
  },
  watch: {
    token(newToken) {
      this.token = newToken;
    },
    user(newUser) {
      this.user = newUser;
    },
    role(newRole) {
      this.role = newRole;
    }
  },
  methods: {
    logout() {
      localStorage.removeItem("token");
      localStorage.removeItem("user");
      localStorage.removeItem("role");
      this.token = null;
      this.user = '';
      this.role = '';
      this.$router.push('/login');
    }
  },
  created() {
    window.addEventListener('storage', this.syncLocalStorage);
  },
  beforeDestroy() {
    window.removeEventListener('storage', this.syncLocalStorage);
  },
  methods: {
    syncLocalStorage(event) {
      if (event.key === 'token' || event.key === 'user' || event.key === 'role') {
        this.token = localStorage.getItem("token");
        this.user = localStorage.getItem("user");
        this.role = localStorage.getItem("role");
      }
    },
    logout() {
      localStorage.removeItem("token");
      localStorage.removeItem("user");
      localStorage.removeItem("role");
      this.syncLocalStorage({ key: 'token' });
      this.syncLocalStorage({ key: 'user' });
      this.syncLocalStorage({ key: 'role' });
      this.$router.push('/login');
    }
  }
}
</script>

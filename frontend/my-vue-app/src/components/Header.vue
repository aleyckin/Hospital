<template>
  <nav v-if="token" class="navbar navbar-expand-lg navbar-light bg-light">
    <div class="container-fluid">
      <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
          <li class="nav-item">
            <router-link to="/profile" class="nav-link">Профиль</router-link>
          </li>
          <li class="nav-item">
            <router-link to="/records" class="nav-link">Записи</router-link>
          </li>
          <li v-if="role === 'ADMIN'" class="nav-item">
            <router-link to="/allrecords" class="nav-link">Все записи</router-link>
          </li>
          <li v-if="role === 'ADMIN'" class="nav-item">
            <router-link to="/doctor" class="nav-link">Врачи</router-link>
          </li>
          <li v-if="role === 'ADMIN'" class="nav-item">
            <router-link to="/users" class="nav-link">Пользователи</router-link>
          </li>
        </ul>
        <div class="navbar-text">
          <span v-if="user" class="me-3">Пользователь: {{ user }}</span>
        </div>
        <button type="button" class="btn btn-danger ms-3" @click="logout">Выход</button>
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

<style scoped>
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

.navbar-text {
  display: flex;
  align-items: center;
  margin-left: auto;
}

.btn {
  font-weight: bold;
}
</style>

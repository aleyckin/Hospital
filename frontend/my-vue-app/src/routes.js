import doctor from './pages/doctor.vue'
import profile from './pages/profile.vue'
import users from "./pages/users.vue";
import login from "./pages/login.vue";
import registration from "./pages/registration.vue";
import Error from "./pages/error.vue";
import resetpassword from "./pages/resetpassword.vue";
import badUser from "./pages/badUser.vue"
import records from "./pages/records.vue"
import {createRouter, createWebHistory} from "vue-router"

import axios from 'axios';

const routes = [
    {path: '/doctor', component: doctor, meta: { requiresAuth: true, requiresAdmin: true }},
    {path: "/users", component: users, meta: { requiresAuth: true, requiresAdmin: true }},
    {path: '/profile', component: profile, meta: { requiresAuth: true }},
    {path: "/login", component: login},
    {path: "/badUser", component: badUser},
    {path: "/registration", component: registration},
    {path: "/records", component: records, meta: { requiresAuth: true }},
    {path: "/reset-password", component: resetpassword, meta: { requiresAuth: true }},
    {path: "/error", component: Error, meta: { requiresAuth: true }},
    {
        path: "/registrationConfirm",
        component: {
            template: '<div></div>',
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
                            this.$router.push('/badUser');
                        });
                }
            }
        }
    }
]

const router = createRouter({
    history: createWebHistory(),
    linkActiveClass: 'active',
    routes
})

router.beforeEach((to, from, next) => {
    const isAuthenticated = localStorage.getItem("token");
    if (to.matched.some((route) => route.meta.requiresAuth)) {
        if (!isAuthenticated) {
            next("/login");
            return;
        }
    }
    const isAdmin = localStorage.getItem("role") === "ADMIN";
    if (to.matched.some((route) => route.meta.requiresAdmin)) {
        if (!isAdmin) {
            next("/error");
            return;
        }
    }
    next();
});

export default router;
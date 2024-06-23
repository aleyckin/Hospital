import doctor from './pages/doctor.vue'
import profile from './pages/profile.vue'
import users from "./pages/users.vue";
import login from "./pages/login.vue";
import registration from "./pages/registration.vue";
import Error from "./pages/error.vue";
import resetpassword from "./pages/resetpassword.vue";
import badUser from "./pages/badUser.vue"
import {createRouter, createWebHistory} from "vue-router"

const routes = [
    {path: '/doctor', component: doctor, meta: { requiresAuth: true, requiresAdmin: true }},
    {path: "/users", component: users, meta: { requiresAuth: true, requiresAdmin: true }},
    {path: '/profile', component: profile, meta: { requiresAuth: true }},
    {path: "/login", component: login},
    {path: "/badUser", component: badUser},
    {path: "/registration", component: registration},
    {path: "/resetPassword", component: resetpassword, meta: { requiresAuth: true }},
    {path: "/error", component: Error, meta: { requiresAuth: true }},
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
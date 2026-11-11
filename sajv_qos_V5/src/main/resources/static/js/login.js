document.addEventListener("DOMContentLoaded", function () {

    const password = document.getElementById("password");
    const toggle = document.getElementById("togglePassword");

    toggle.addEventListener("click", function () {

        const visible = password.type === "text";

        password.type = visible ? "password" : "text";

        toggle.innerHTML = visible
            ? '<i class="fa-regular fa-eye-slash"></i>'
            : '<i class="fa-regular fa-eye"></i>';

        toggle.setAttribute(
            "aria-label",
            visible ? "Mostrar contraseña" : "Ocultar contraseña"
        );
    });

});
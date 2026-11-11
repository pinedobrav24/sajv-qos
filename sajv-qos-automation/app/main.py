from fastapi import FastAPI

from app.routers import qos_router, router_router
from app.services.conexion import settings

app = FastAPI(
    title=settings.app_name,
    version=settings.app_version,
    description="Servicio de automatización temporal de políticas QoS para SAJV"
)

@app.get("/")
def inicio():
    return {
        "aplicacion": settings.app_name,
        "version": settings.app_version,
        "estado": "OPERATIVO"
    }

@app.get("/api/health")
def health():
    return {
        "estado": "OK",
        "modo": "SIMULACION" if settings.qos_simulation else "REAL"
    }

app.include_router(qos_router.router)
app.include_router(router_router.router)

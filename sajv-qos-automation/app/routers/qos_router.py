from fastapi import APIRouter, HTTPException

from app.schemas.qos_schema import QosOperacionRequest
from app.services.qos_service import qos_service

router = APIRouter(
    prefix="/api/qos",
    tags=["Automatización QoS"]
)

@router.post("/activar")
def activar_qos(datos: QosOperacionRequest):
    resultado = qos_service.activar(datos)
    if not resultado["exitoso"]:
        raise HTTPException(status_code=502, detail=resultado["mensaje"])
    return resultado

@router.post("/desactivar")
def desactivar_qos(datos: QosOperacionRequest):
    resultado = qos_service.desactivar(datos)
    if not resultado["exitoso"]:
        raise HTTPException(status_code=502, detail=resultado["mensaje"])
    return resultado

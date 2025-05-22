from typing import List
from pydantic import BaseModel

class ConsentStockInitiator(BaseModel):
    id: str
    name: str
    total: int

class ConsentStockOrganization(BaseModel):
    id: str
    name: str
    initiators: List[ConsentStockInitiator]

class ConsentStockFilePayload(BaseModel):
    totalPF: int
    totalPJ: int
    organizations: List[ConsentStockOrganization]

import { Injectable } from "@nestjs/common";
import { RoleRepositoryPort } from "src/domain/repositories/role.repository.port";
import { RoleModel } from "src/domain/models/role.model";
import { PrismaClient } from '@prisma/client';

@Injectable()    
export class PrismaRoleRepositoryAdapter implements RoleRepositoryPort {
    constructor(private prisma: PrismaClient) {}

    
    createRole(role: RoleModel): Promise<RoleModel> {
        throw new Error("Method not implemented.");
    }
   


    
}
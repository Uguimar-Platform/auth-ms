
import { Injectable } from "@nestjs/common";
import { UserModel } from "src/domain/models/user.model";
import { PrismaClient } from '@prisma/client';
import { RoleRepositoryPort } from "src/domain/repositories/role.repository.port";
import { RoleModel } from "src/domain/models/role.model";

@Injectable()    
export class PrismaRoleRepositoryAdapter implements RoleRepositoryPort {
    constructor(private prisma: PrismaClient) {}

    
    createRole(role: RoleModel): Promise<RoleModel> {
        throw new Error("Method not implemented.");
    }
   


    
}
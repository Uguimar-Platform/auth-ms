import { Injectable } from "@nestjs/common";
import { PermissionsModel } from "src/domain/models/permissions.model";
import { PermissionsRepositoryPort } from "src/domain/repositories/permission.repository.port";

@Injectable()    
export class PrismaPermissionRepositoryAdapter implements PermissionsRepositoryPort {
    
    createPermission(permisison: PermissionsModel): Promise<PermissionsModel> {
        throw new Error("Method not implemented.");
    }
   
    
   


    
}
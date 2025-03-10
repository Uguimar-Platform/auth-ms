import { Inject, Injectable } from '@nestjs/common';
import { PermissionsModel } from 'src/domain/models/permissions.model';
import { UserModel } from 'src/domain/models/user.model';
import { PermissionsRepositoryPort } from 'src/domain/repositories/permission.repository.port';

@Injectable()
export class CreatePermissionsUseCase {
  constructor(
    @Inject('PermissionRepository') private permissionRepository: PermissionsRepositoryPort,
  ) {}


  async execute(permission:PermissionsModel){ 
        const createdPermission = await this.permissionRepository.createPermission(permission);
        return createdPermission;
    }
}
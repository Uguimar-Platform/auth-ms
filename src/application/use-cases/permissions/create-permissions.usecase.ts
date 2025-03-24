import { HttpStatus, Inject, Injectable } from '@nestjs/common';
import { PermissionsModel } from 'src/domain/models/permissions.model';
import { PermissionsRepositoryPort } from 'src/domain/repositories/permission.repository.port';

@Injectable()
export class CreatedPermissionsUseCase {
  constructor(
    @Inject('PermissionRepository')
    private permissionRepository: PermissionsRepositoryPort,
  ) {}

  async execute(permission: PermissionsModel) {
    this.permissionRepository.createPermission(permission);
    return {
      status: HttpStatus.CREATED,
      message: 'Permission created successfully',
    };
  }
}

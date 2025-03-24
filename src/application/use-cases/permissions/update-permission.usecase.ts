import { HttpStatus, Inject, Injectable } from "@nestjs/common";
import { PermissionsModel } from "src/domain/models/permissions.model";
import { PermissionsRepositoryPort } from "src/domain/repositories/permission.repository.port";

@Injectable()
export class UpdatePermissionUseCase {
  constructor(
    @Inject('PermissionRepository')
    private permissionRepository: PermissionsRepositoryPort,
  ) {}

  async execute(id: number, updatePermission: Partial<PermissionsModel>) {
    await this.permissionRepository.updatePermission(id, updatePermission);
    return {
      status: HttpStatus.OK,
      message: 'Permission updated successfully',
    };
  }
}

import { HttpStatus, Inject, Injectable } from "@nestjs/common";
import { RoleModel } from "src/domain/models/role.model";
import { RoleRepositoryPort } from "src/domain/repositories/role.repository.port";

@Injectable()
export class UpdateRoleUseCase {
  constructor(
    @Inject('RoleRepository') private roleRepository: RoleRepositoryPort,
  ) {}

  async execute(id: number, role: Partial<RoleModel>) {
    await this.roleRepository.updateRole(id, role);
    return {
      status: HttpStatus.OK,
      message: 'Role updated successfully',
    };
  }
}

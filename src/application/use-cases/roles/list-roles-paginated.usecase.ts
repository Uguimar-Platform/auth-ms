import { HttpStatus, Inject, Injectable } from "@nestjs/common";
import { RoleModel } from "src/domain/models/role.model";
import { RoleRepositoryPort } from "src/domain/repositories/role.repository.port";

@Injectable()
export class ListRolesPaginatedUseCase {
  /**
   *
   * @param roleRepository  Repository to inject into providers
   *    */
  constructor(
    @Inject('ModuleRepository') private roleRepository: RoleRepositoryPort,
  ) {}

  /**
   * Method that implements the logic to create a module.
   * It is responsible for creating a module in the database.
   *
   * @param module The module to be created.
   * @returns The status and a message indicating that the module was created successfully.
   */

  async execute(filter: RoleModel) {
    const roleexecute =
      await this.roleRepository.listRolePaginated(filter);
    return {
      status: HttpStatus.OK,
      data: roleexecute,
    };
  }
}
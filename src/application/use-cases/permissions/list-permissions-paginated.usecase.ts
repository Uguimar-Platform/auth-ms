import { HttpStatus, Inject, Injectable } from "@nestjs/common";
import { PermissionsModel } from "src/domain/models/permissions.model";
import { PermissionsRepositoryPort } from "src/domain/repositories/permission.repository.port";
import { PaginatedFilterDto } from "src/infrastructure/dto/list-module.filter.dto";

@Injectable()
export class ListPermissionsPaginatedUseCase {
  /**
   *
   * @param roleRepository  Repository to inject into providers
   *    */
  constructor(
    @Inject('PermissionRepository')
    private permissionRepository: PermissionsRepositoryPort,
  ) {}

  /**
   * Method that implements the logic to create a module.
   * It is responsible for creating a module in the database.
   *
   * @param module The module to be created.
   * @returns The status and a message indicating that the module was created successfully.
   */

  async execute(filter: PaginatedFilterDto) {
    const permissionexecute =
      await this.permissionRepository.listPermissionPaginated(filter);
    return {
      status: HttpStatus.OK,
      data: permissionexecute,
    };
  }
}
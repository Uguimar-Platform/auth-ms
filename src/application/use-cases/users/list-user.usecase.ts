import { HttpStatus, Inject, Injectable } from "@nestjs/common";
import { UserModel } from "src/domain/models/user.model";
import { UserRepositoryPort } from "src/domain/repositories/user.repository.port";
import { PaginatedFilterDto } from "src/infrastructure/dto/list-module.filter.dto";

@Injectable()
export class ListUserPaginatedUseCase {
  /**
   *
   * @param roleRepository  Repository to inject into providers
   *    */
  constructor(
     @Inject('UserRepository') private userRepository: UserRepositoryPort,
   ) {}

  /**
   * Method that implements the logic to create a module.
   * It is responsible for creating a module in the database.
   *
   * @param module The module to be created.
   * @returns The status and a message indicating that the module was created successfully.
   */

  async execute(filter: PaginatedFilterDto) {
    const roleexecute =
      await this.userRepository.listUserPaginated(filter);
    return {
      status: HttpStatus.OK,
      data: roleexecute,
    };
  }
}
import { HttpStatus, Inject, Injectable } from "@nestjs/common";
import { UserRepositoryPort } from "src/domain/repositories/user.repository.port";

@Injectable()
export class UpdateStatusUserUseCase {
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
 
  async execute(id: number, status: number) {
    await this.userRepository.updateUserStatus(
      id,
      status,
    );
    return {
      status: HttpStatus.CREATED,
      message: 'User changed status successfully',
    };
  }
}
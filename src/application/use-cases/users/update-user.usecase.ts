import { HttpStatus, Inject, Injectable } from "@nestjs/common";
import { UserModel } from "src/domain/models/user.model";
import { UserRepositoryPort } from "src/domain/repositories/user.repository.port";

@Injectable()
export class UpdateUserUseCase {
  constructor(
    @Inject('UserRepository') private userRepository: UserRepositoryPort,
  ) {}

  async execute(id: number, user: Partial<UserModel>) {
    await this.userRepository.updateUser(id, user);
    return {
      status: HttpStatus.OK,
      message: 'User updated successfully',
    };
  }
}
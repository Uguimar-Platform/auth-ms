import { Inject, Injectable } from '@nestjs/common';
import { UserModel } from 'src/domain/models/user.model';
import { UserRepositoryPort } from 'src/domain/repositories/user.repository.port';

@Injectable()
export class LoginUserUseCase {
  constructor(
    @Inject('UserRepository') private userRepository: UserRepositoryPort,
  ) {}

  async execute(email: string, password: string) {
    return this.userRepository.loginUser(email, password);
  }
}
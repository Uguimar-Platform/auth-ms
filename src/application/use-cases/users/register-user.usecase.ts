import { HttpStatus, Inject, Injectable } from '@nestjs/common';
import { UserModel } from 'src/domain/models/user.model';
import { UserRepositoryPort } from 'src/domain/repositories/user.repository.port';

@Injectable()
export class RegisterUserUseCase {
  constructor(
    @Inject('UserRepository') private userRepository: UserRepositoryPort,
  ) {}


  async execute(user:UserModel){ 
        await this.userRepository.createUser(user);
        return {
          status: HttpStatus.CREATED,
          message: 'User created successfully',
        };
    }
}
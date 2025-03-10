import { Inject, Injectable } from '@nestjs/common';
import { UserModel } from 'src/domain/models/user.model';
import { UserRepositoryPort } from 'src/domain/repositories/user.repository.port';

@Injectable()
export class RegisterUserUseCase {
  constructor(
    @Inject('UserRepository') private userRepository: UserRepositoryPort,
  ) {}

  /*async execute(user:UserModel):Promise<UserModel>{ 
       const createdUser = await this.userRepository.createUser(user);
        return createdUser; 

        return 'createdUser';

    }*/

  async execute(user:UserModel){ 
        return 'createdUser';
    }
}
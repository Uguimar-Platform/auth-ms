import { Inject, Injectable } from '@nestjs/common';
import { UserModel } from 'src/domain/models/user.model';

@Injectable()
export class CreateRoleUseCase {
  constructor(
    //@Inject('UserRepository') private userRepository: UserRepositoryPort,
  ) {}

  /*async execute(user:UserModel):Promise<UserModel>{ 
       const createdUser = await this.userRepository.createUser(user);
        return createdUser; 

        return 'createdUser';

    }*/

  async execute(role:any){ 
        return 'createdUser';
    }
}
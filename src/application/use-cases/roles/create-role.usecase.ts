import { HttpStatus, Inject, Injectable } from '@nestjs/common';
import { RoleModel } from 'src/domain/models/role.model';
import { RoleRepositoryPort } from 'src/domain/repositories/role.repository.port';

@Injectable()
export class CreateRoleUseCase {
  constructor(
    @Inject('RoleRepository') private roleRepository: RoleRepositoryPort,
  ) {}



  async execute(role:RoleModel){
   await this.roleRepository.createRole(role);
  
    return{
      status: HttpStatus.CREATED,
      message: 'Role created successfully',
    }
  }
          
  
}
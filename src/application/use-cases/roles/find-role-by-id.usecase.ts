import { Inject, Injectable } from '@nestjs/common';
import { RoleRepositoryPort } from 'src/domain/repositories/role.repository.port';

@Injectable()
export class FindRoleByIdUseCase {
  /**
   *
   * @param moduleRepository  Repository to inject into providers
   *    */
  constructor(
    @Inject('RoleRepository') private roleRepository: RoleRepositoryPort,
  ) {}

  /**
   * 
   * @param id  The id of the module to be found.
   * @returns  The module found.
   */

  async execute(id: number) {
    const role = await this.roleRepository.findRoleById(id);
    return role;
  }
}

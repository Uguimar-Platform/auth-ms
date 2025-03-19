import { Inject, Injectable } from '@nestjs/common';
import { ModuleRepositoryPort } from 'src/domain/repositories/module.repository.port';

@Injectable()
export class FindModuleByIdUseCase {
  /**
   *
   * @param moduleRepository  Repository to inject into providers
   *    */
  constructor(
    @Inject('ModuleRepository') private moduleRepository: ModuleRepositoryPort,
  ) {}

  /**
   * 
   * @param id  The id of the module to be found.
   * @returns  The module found.
   */

  async execute(id: number) {
    const module = await this.moduleRepository.findModuleById(id);
    return module;
  }
}

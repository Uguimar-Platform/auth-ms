import { Inject, Injectable, HttpStatus } from '@nestjs/common';
import { stat } from 'fs';
import { ModuleModel } from 'src/domain/models/module.model';
import { PaginatedModuleModel } from 'src/domain/models/paginated.model';
import { ModuleRepositoryPort } from 'src/domain/repositories/module.repository.port';
import { ModuleFilterDto } from 'src/infrastructure/dto/module-filter.dto';

@Injectable()
export class ListModulePaginatedUseCase {
  /**
   *
   * @param moduleRepository  Repository to inject into providers
   *    */
  constructor(
    @Inject('ModuleRepository') private moduleRepository: ModuleRepositoryPort,
  ) {}

  /**
   * Method that implements the logic to create a module.
   * It is responsible for creating a module in the database.
   *
   * @param module The module to be created.
   * @returns The status and a message indicating that the module was created successfully.
   */

  async execute(filter: ModuleFilterDto) {
    const moduleexecute =
      await this.moduleRepository.listModulePaginated(filter);
    return {
      status: HttpStatus.OK,
      data: moduleexecute,
    };
  }
}
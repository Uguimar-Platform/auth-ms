import { ModuleFilterDto } from "src/infrastructure/dto/modules/module-filter.dto";
import { ModuleModel } from "../models/module.model";
import { PaginatedFilterDto } from "src/infrastructure/dto/list-module.filter.dto";

/**
 * Interface that represents the module repository port.
 * @interface createModule Method that creates a module.
 * @interface findModuleById Method that finds a module by id.
 */

export interface ModuleRepositoryPort {
  createModule(module: ModuleModel);
  findModuleById(id: number);
  updateModule(id: number, module: ModuleModel);
  updateModuleStatus(id: number, status: number);
  listModulePaginated(filter: PaginatedFilterDto);
}
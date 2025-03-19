import { ModuleFilterDto } from "src/infrastructure/dto/module-filter.dto";
import { ModuleModel } from "../models/module.model";
import { PaginatedModuleModel } from "../models/paginated.model";

/**
 * Interface that represents the module repository port.
 * @interface createModule Method that creates a module.
 * @interface findModuleById Method that finds a module by id.
 */

export interface ModuleRepositoryPort {
  createModule(module: ModuleModel): Promise<ModuleModel>;
  findModuleById(id: number): Promise<ModuleModel>;
  updateModule(id: number, module: ModuleModel): Promise<ModuleModel>;
  updateModuleStatus(id: number, status: number): Promise<ModuleModel>;
  listModulePaginated(filter: ModuleFilterDto): Promise<PaginatedModuleModel>;
}
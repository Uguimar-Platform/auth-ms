import { ModuleModel } from "../models/module.model";

/**
 * Interface that represents the module repository port.
 * @interface createModule Method that creates a module.
 * @interface findModuleById Method that finds a module by id.
 */

export interface ModuleRepositoryPort {
  createModule(module: ModuleModel): Promise<ModuleModel>;
  findModuleById(id: number): Promise<ModuleModel>;
}
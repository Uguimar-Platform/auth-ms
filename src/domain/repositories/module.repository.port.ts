import { ModuleModel } from "../models/module.model";

/**
 * Interface that represents the module repository port.
 */

export interface ModuleRepositoryPort {
  createModule(module: ModuleModel): Promise<ModuleModel>;
}
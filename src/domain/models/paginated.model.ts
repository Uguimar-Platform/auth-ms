import { ModuleModel } from "./module.model";

export interface PaginatedModuleModel {
  data: ModuleModel[];
  totalPages: number;
  totalItems: number;
  page: number;
  limit: number;
}
